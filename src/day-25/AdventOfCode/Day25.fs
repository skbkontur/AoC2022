let inline unreadable ():'a = failwith "unreachable!"

let snafuToDecimal number =
    let mapSnafu n : int64 =
        match n with
        | '0' -> 0
        | '1' -> 1
        | '2' -> 2
        | '-' -> -1
        | '=' -> -2
        | _ -> unreadable ()

    let _, sum =
        let sumAndPowerFolder chr (index, sum) =
            (index + 1,
             sum
             + (mapSnafu chr) * (int64 (5.0 ** double index)))

        Seq.foldBack sumAndPowerFolder number (0, 0)

    sum

assert (snafuToDecimal "1=-0-2"         = 1747)
assert (snafuToDecimal "1"              = 1)
assert (snafuToDecimal "2"              = 2)
assert (snafuToDecimal "1121-1110-1=0"  = 314159265)

let decimalToSnafu (number: int64) =
    let rec inner res (n: int64) =
        match n % 5L, n with
        | 0L, 0L -> res
        | 0L, r -> inner (res @ [ '0' ]) (r / 5L)
        | 1L, r -> inner (res @ [ '1' ]) (r / 5L)
        | 2L, r -> inner (res @ [ '2' ]) (r / 5L)
        | 3L, r -> inner (res @ [ '=' ]) ((r + 2L) / 5L)
        | 4L, r -> inner (res @ [ '-' ]) ((r + 1L) / 5L)
        | _ -> unreadable ()

    (inner List.empty<char> number) |> Seq.rev

open System.IO

File.ReadLines("day25.txt")
|> Seq.map snafuToDecimal
|> Seq.sum
|> decimalToSnafu
|> Seq.toArray
|> System.String
|> printfn "%s"