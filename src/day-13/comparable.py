from typing import Callable, Self

__all__ = ["Comparable"]

class Comparable:
    def __init__(self, value: list|int):
        if isinstance(value, list):
            value = [Comparable(item) for item in value]
        self.value = value

    def _convert_to_list(self) -> list:
        if isinstance(self.value, int):
            return [Comparable(self.value)]
        return self.value

    def _compare(self, other: Self, comparator: Callable[[Self, Self], bool]) -> bool:
        if isinstance(self.value, int) and isinstance(other.value, int):
            return comparator(self.value, other.value)

        left = self._convert_to_list()
        right = other._convert_to_list()

        return comparator(left, right)

    def __eq__(self, other: Self) -> bool:
        return self._compare(other, lambda left, right: left == right)

    def __lt__(self, other: Self) -> bool:
        return self._compare(other, lambda left, right: left < right)
