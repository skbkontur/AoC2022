use std::cmp::max;

use nom::{
    bytes::complete::tag,
    character::{self, complete::line_ending},
    combinator::{all_consuming, map},
    multi::separated_list1,
    sequence::{preceded, separated_pair},
    IResult,
};
use num::Complex;

fn main() {
    let (_, reading) = parse(include_str!("input.txt").trim_end()).unwrap();
    dbg!(part1(&reading));
    dbg!(part2(&reading));
}

type Point = Complex<i64>;

fn point(input: &str) -> IResult<&str, Point> {
    map(
        separated_pair(
            preceded(tag("x="), character::complete::i64),
            tag(", "),
            preceded(tag("y="), character::complete::i64),
        ),
        |(x, y)| Complex::new(x, y),
    )(input)
}
fn sensor(input: &str) -> IResult<&str, Point> {
    preceded(tag("Sensor at "), point)(input)
}
fn beacon(input: &str) -> IResult<&str, Point> {
    preceded(tag("closest beacon is at "), point)(input)
}
fn parse(input: &str) -> IResult<&str, Vec<(Point, Point)>> {
    all_consuming(separated_list1(
        line_ending,
        separated_pair(sensor, tag(": "), beacon),
    ))(input)
}

fn generate_ranges(reading: &[(Point, Point)], target_im: i64) -> Vec<(i64, i64)> {
    let mut ranges = Vec::new();

    for (sensor, beacon) in reading {
        let radius = (beacon - sensor).l1_norm();
        let distance_im = (target_im - sensor.im).abs();
        if distance_im < radius {
            let leftover_re = radius - distance_im;
            let start = sensor.re - leftover_re;
            let end = sensor.re + leftover_re;
            ranges.push((start, end));
        }
    }

    ranges.sort();
    let mut i = 0;
    while i < ranges.len() - 1 {
        if ranges[i].1 >= ranges[i + 1].0 {
            ranges[i].1 = max(ranges[i + 1].1, ranges[i].1);
            ranges.remove(i + 1);
            continue;
        }
        i += 1;
    }

    ranges
}

fn part1(reading: &[(Point, Point)]) -> i64 {
    generate_ranges(reading, 2_000_000)
        .iter()
        .map(|range| range.1 - range.0)
        .sum()
}

fn part2(reading: &[(Point, Point)]) -> i64 {
    let upper_bound = 4_000_000;
    for im in 0..=upper_bound {
        let ranges = generate_ranges(reading, im);
        match (ranges.len(), ranges[0].0, ranges[0].1) {
            (1, start, end) if start <= 0 && end >= upper_bound => {
                continue;
            }
            (1, start, ..) if start > 0 => return 0 * upper_bound + im,
            (1, .., end) if end < upper_bound => return upper_bound * upper_bound + im,
            (2, .., end) => return (end + 1) * upper_bound + im,
            _ => unreachable!(),
        };
    }
    unreachable!()
}
