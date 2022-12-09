class Point(tuple):
    def __add__(self, other):
        return Point(x + y for x, y in zip(self, other))
    def __sub__(self, other):
        return Point(x - y for x, y in zip(self, other))
    def __len__(self) -> int:
         return int((self[0] * self[0] + self[1] * self[1]) ** 0.5)

directions = {
    "R": (1, 0),
    "L": (-1, 0),
    "U": (0, 1),
    "D": (0, -1),
}

with open("sample2.txt", "r") as f:
    moves = [(move[0], int(move[1])) for move in [x.removesuffix("\n").split() for x in f.readlines()]]

def sign(a):
    return 0 if a == 0 else a // abs(a)

def get_move(a): 
    return (sign(a[0]), sign(a[1])) if len(a) > 1 else (0,0)

knots = [Point((0,0)) for _ in range(10)]
visited = {knots[-1]}

for (direction, count) in moves:    
    for i in range(count):
        knots[0] += directions[direction]

        for i in range(1, len(knots)):
            knots[i] += get_move(knots[i-1] - knots[i])

        visited.add(knots[-1])

print(len(visited))