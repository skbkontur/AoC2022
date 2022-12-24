import collections
import itertools
from collections import defaultdict, namedtuple
from dataclasses import dataclass, field


class Vector(namedtuple('Vector', ('x', 'y'))):
    def get_surrounding_vectors(self) -> set['Vector']:
        return {
            Vector(self.x + 1, self.y),
            Vector(self.x, self.y + 1),
            Vector(self.x - 1, self.y),
            Vector(self.x, self.y - 1),
        }


@dataclass
class BlizzardNavigator:
    width: int
    height: int
    horizontal_blizzards: dict[int, list[tuple[Vector, Vector]]] = field(default_factory=lambda: defaultdict(list))
    vertical_blizzards: dict[int, list[tuple[Vector, Vector]]] = field(default_factory=lambda: defaultdict(list))
    _prepared_vectors = defaultdict(dict)

    BLIZZARD_TYPES = {
        ">": Vector(1, 0),
        "<": Vector(-1, 0),
        "v": Vector(0, 1),
        "^": Vector(0, -1),
    }

    def check_blizzard(self, position: Vector, step: int) -> bool:
        for start_position, blizzard in itertools.chain(
            self.horizontal_blizzards[position.y],
            self.vertical_blizzards[position.x],
        ):
            _x = (start_position.x + blizzard.x * step) % self.width
            _y = (start_position.y + blizzard.y * step) % self.height
            if (v := self._prepared_vectors[_y].get(_x)) is None:
                self._prepared_vectors[_y][_x] = v = Vector(_x, _y)
            if position == v:
                return True
        return False

    @classmethod
    def parse(cls, value: str) -> tuple['BlizzardNavigator', Vector, Vector]:
        rows = list(map(str.strip, value.strip().split('\n')))
        navigator = BlizzardNavigator(width=len(rows[0]) - 2, height=len(rows) - 2)

        for y, row in enumerate(rows[1:-1]):
            for x, char in enumerate(row[1:-1]):
                if blizzard := cls.BLIZZARD_TYPES.get(char):
                    if blizzard.x:
                        navigator.horizontal_blizzards[y].append((Vector(x, y), blizzard))
                    elif blizzard.y:
                        navigator.vertical_blizzards[x].append((Vector(x, y), blizzard))

        start = Vector(rows[0].index('.') - 1, -1)
        finish = Vector(rows[-1].index('.') - 1, navigator.height)
        return navigator, start, finish

    def get_path_length(self, start_position: Vector, finish_position: Vector, start_step: int = 0) -> int:
        queue = collections.deque()
        queue.append((start_position, start_step))
        visited = {(start_position, start_step)}

        while queue:
            current_position, step = queue.popleft()  # type: Vector, int
            if current_position == finish_position:
                return step

            for next_position in current_position.get_surrounding_vectors():
                _next = (next_position, step + 1)
                if (
                        (0 <= next_position.x < self.width and 0 <= next_position.y < self.height)
                        or next_position == finish_position
                ) and not self.check_blizzard(*_next) and _next not in visited:
                    queue.append(_next)
                    visited.add(_next)

            _next = (current_position, step + 1)
            if not self.check_blizzard(*_next) and _next not in visited:
                queue.append(_next)
                visited.add(_next)


if __name__ == '__main__':
    with open('input.txt') as f:
        text = f.read()

    blizzard_navigator, _start, _finish = BlizzardNavigator.parse(text)
    # First star
    result = blizzard_navigator.get_path_length(_start, _finish, 0)
    print('[FIRST] Minutes', result)

    # Second star
    result_2 = blizzard_navigator.get_path_length(_finish, _start, result)
    result_3 = blizzard_navigator.get_path_length(_start, _finish, result_2)
    print('[SECOND] Minutes', result_3)
