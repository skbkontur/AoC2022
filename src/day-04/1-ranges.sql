WITH t0 AS (
  SELECT
    *,
    ARRAY(
      SELECT CAST(n AS INT)
      FROM UNNEST(REGEXP_SPLIT_TO_ARRAY(input, '[-,]')) AS n
    ) AS bounds
  FROM day_4
),

t1 AS (
  SELECT
    *,
    --   1     3     4     2
    -- 1 [ 2 3 { 4 5 } 6 7 ] 8 9
    NUMRANGE(bounds[1], bounds[2], '[]') @> NUMRANGE(bounds[3], bounds[4], '[]') AS contains_left,
    --   3     1     2     4
    -- 1 [ 2 3 { 4 5 } 6 7 ] 8 9
    NUMRANGE(bounds[1], bounds[2], '[]') <@ NUMRANGE(bounds[3], bounds[4], '[]') AS contains_right
  FROM t0
)

-- SELECT COUNT(*) FILTER(WHERE contains_left OR contains_right)
SELECT SUM(CASE WHEN contains_left OR contains_right THEN 1 END)
FROM t1
