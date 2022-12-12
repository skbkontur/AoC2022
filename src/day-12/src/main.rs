use std::{
    collections::{HashSet, VecDeque},
    fs,
    io::BufRead,
    io::BufReader,
};

#[derive(PartialEq, Eq, Hash, Clone, Debug)]
struct Position {
    row: usize,
    column: usize,
}

struct PositionWithPathLen {
    position: Position,
    path_len: u16,
}

struct HeightMap {
    width: usize,
    height: usize,
    start: Position,
    end: Position,
    data: Vec<Vec<u8>>,
}

impl HeightMap {
    pub fn new(buf_reader: BufReader<fs::File>) -> HeightMap {
        let mut width = 0;
        let mut height = 0;
        let mut start = Position { row: 0, column: 0 };
        let mut end = Position { row: 0, column: 0 };
        let mut data = Vec::new();

        for (row_index, line) in buf_reader.lines().flatten().enumerate() {
            let mut row = Vec::new();

            for (column_index, c) in line.chars().enumerate() {
                match c {
                    'S' => {
                        start = Position {
                            row: row_index,
                            column: column_index,
                        };
                        row.push(0)
                    }
                    'E' => {
                        end = Position {
                            row: row_index,
                            column: column_index,
                        };
                        row.push(25)
                    }
                    _ => row.push(c as u8 - 'a' as u8),
                }
            }

            data.push(row);
            width = line.len();
            height += 1;
        }

        HeightMap {
            width,
            height,
            start,
            end,
            data,
        }
    }

    pub fn get_height(&self, position: &Position) -> u8 {
        self.data[position.row][position.column]
    }

    fn get_neighbours(&self, position: &Position) -> Vec<Position> {
        let mut neighbours = Vec::new();

        for shift in vec![(0, 1), (0, -1), (1, 0), (-1, 0)] {
            let row = position.row as i16 + shift.0;
            let column = position.column as i16 + shift.1;

            if row >= 0 && row < self.height as i16 && column >= 0 && column < self.width as i16 {
                neighbours.push(Position {
                    row: row as usize,
                    column: column as usize,
                });
            }
        }
        neighbours
    }

    pub fn get_shortest_path_len<IsFinished, CanMakeAStep>(
        &self,
        from: &Position,
        is_finished: IsFinished,
        can_make_step: CanMakeAStep,
    ) -> u16
    where
        IsFinished: Fn(&Position) -> bool,
        CanMakeAStep: Fn(&Position, &Position) -> bool,
    {
        let mut queue = VecDeque::new();
        let mut visited = HashSet::new();

        queue.push_back(PositionWithPathLen {
            position: from.clone(),
            path_len: 0,
        });
        visited.insert(from.clone());

        while !queue.is_empty() {
            let current = queue.pop_front().unwrap();
            if is_finished(&current.position) {
                return current.path_len;
            }

            for neighbour in self
                .get_neighbours(&current.position)
                .iter()
                .filter(|neighbour| can_make_step(neighbour, &current.position))
            {
                if !visited.contains(&neighbour) {
                    queue.push_back(PositionWithPathLen {
                        position: neighbour.clone(),
                        path_len: current.path_len + 1,
                    });
                    visited.insert(neighbour.clone());
                }
            }
        }

        panic!("No path found from {:?}", from)
    }

    pub fn get_shortest_path_len_start_to_end(&self) -> u16 {
        self.get_shortest_path_len(
            &self.end,
            |position| self.get_height(&position) == 0,
            |neighbour, current| self.get_height(&neighbour) + 1 >= self.get_height(&current),
        )
    }
}

fn main() {
    let buf_reader = BufReader::new(fs::File::open("input").unwrap());
    let height_map = HeightMap::new(buf_reader);
    println!("{}", height_map.get_shortest_path_len_start_to_end());
}
