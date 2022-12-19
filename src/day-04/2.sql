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
    --   1     3   2   4   
    -- 1 [ 2 3 { 4 ] 5 } 6 7 8 9
    bounds[1] <= bounds[4] AND bounds[2] >= bounds[3] AS overlap
  FROM t0
)

-- SELECT COUNT(*) FILTER(WHERE overlap)
SELECT SUM(CASE WHEN overlap THEN 1 END)
FROM t1
