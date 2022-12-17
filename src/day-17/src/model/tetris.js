class Tetris {
    _columnsCount = 7;
    _sequence = ['-', '+', 'J', 'I', 'O'];
    _rocks = {
        '-': [
            ['@','@','@','@'],
        ],
        '+': [
            ['.','@','.'],
            ['@','@','@'],
            ['.','@','.'],
        ],
        // reversed
        'J': [
            ['@','@','@'],
            ['.','.','@'],
            ['.','.','@'],
        ],
        'I': [
            ['@'],
            ['@'],
            ['@'],
            ['@']
        ],
        'O': [
            ['@','@'],
            ['@','@'],
        ],
    };
    _leftPositionOffset = 2;
    _topPositionOffset = 4;

    _tickIndex = 0;
    _rockIndex = 0;
    _rocksCount = 0;
    _movesPattern = [];
    _map = [];

    _snapshotMapSize = 100;
    _snapshots = {};

    constructor({ rocksCount, movesPattern, ticksCount }) {
        this._rocksCount = rocksCount;
        this._movesPattern = movesPattern.split('');
        this._ticksCount = ticksCount;

        this._map.push(Array(this._columnsCount).fill('-'));
    }

    _pureGetNextRock = () => {
        return this._sequence[this._rockIndex];
    }

    _getNextRock = () => {
        const rock = this._sequence[this._rockIndex % this._sequence.length];
        this._rockIndex++;

        return rock;
    }

    _getCurrentMove = () => {
        return this._movesPattern[this._tickIndex % this._movesPattern.length];
    }

    _getEmptyRow = () => {
        return Array(this._columnsCount).fill('.');
    }

    _putRockOnMap = ({ rock, row, column }) => {
        const rockMap = this._rocks[rock];
        const rockHeight = rockMap.length;
        const newMapHeight = row + rockHeight;

        for (let i = this._map.length; i < newMapHeight; i++) {
            this._map.push(this._getEmptyRow());
        }

        for (let i = 0; i < rockMap.length; i++) {
            for (let j = 0; j < rockMap[i].length; j++) {
                this._map[row + i][column + j] = rockMap[i][j];
            }
        }
    }

    _cloneMap = () => {
        return this._map.map(row => row.slice());
    }

    _tryMove = (move) => {
        const newMap = this._cloneMap();

        for (let i = 0; i < newMap.length; i++) {
            const fromLeftToRight = move === '<';
            for (let rawJ = 0; rawJ < newMap[i].length; rawJ++) {
                const j = fromLeftToRight ? rawJ : newMap[i].length - 1 - rawJ;
                const item = newMap[i][j];
                if (item === '@') {
                    const shift = move === '>' ? 1 : -1;
                    const shiftedJ = j + shift;
                    if (newMap[i][shiftedJ] === '.') {
                        newMap[i][j] = '.';
                        newMap[i][shiftedJ] = '@';
                    } else {
                        return undefined;
                    }
                }
            }
        }

        this._map = newMap;
    }

    _tryFall = () => {
        const newMap = this._cloneMap();

        for (let i = 0; i < newMap.length; i++) {
            for (let j = 0; j < newMap[i].length; j++) {
                const item = newMap[i][j];
                if (item === '@') {
                    const downItem = newMap[i - 1][j];
                    if (downItem === '.') {
                        newMap[i - 1][j] = '@';
                        newMap[i][j] = '.';
                    } else {
                        return false;
                    }
                }
            }
        }

        this._map = newMap;
        return true;
    }

    _stopMoving = () => {
        this._map.forEach((row, i) => row.forEach((column, j) => {
            if (this._map[i][j] === '@') {
                this._map[i][j] = '#';
            }
        }))
    }

    _getSnapshot = () => {
        // Информация о состоянии, при совпадении которой с большой вероятностью получился цикл
        return [
            this.getStringifiedMap().split('\n').slice(0, this._snapshotMapSize).join('\n'),
            this._getCurrentMove(),
            this._pureGetNextRock()
        ].join('\n--\n')
    }

    _takeSnapshot = () => {
        const snapshot = this._getSnapshot();
        const data = {
            snapshot,
            tickIndex: this._tickIndex,
            rockIndex: this._rockIndex,
            height: this.getHeight()
        };

        if (!this._snapshots[snapshot]) {
            this._snapshots[snapshot] = [];
        }

        this._snapshots[snapshot].push(data);
    };

    _addRock = (rock) => {
        // TODO optimize landing
        // TODO optimize search
        const lastRockRow = this._map.length - 1 - this._map.slice().reverse().findIndex(row => row.some(column => column !== '.'));
        const row = lastRockRow + this._topPositionOffset;
        const column = this._leftPositionOffset;

        this._putRockOnMap({ rock, row, column });

        while (true) {
            if (this._ticksCount <= this._tickIndex) {
                break;
            }

            const move = this._getCurrentMove();
            this._tryMove(move);
            const isSuccess = this._tryFall();

            this._tickIndex++;

            if (!isSuccess) {
                break;
            }
        }

        this._stopMoving();
        this._takeSnapshot();
    }

    _runRock = () => {
        const rock = this._getNextRock();
        this._addRock(rock);
    }

    _getTrimmedMap = () => {
        let emptyRowsCount = 0;
        while (true) {
            const isEmptyRow = this._map[this._map.length - 1 - emptyRowsCount].every(column => column === '.');

            if (!isEmptyRow) {
                break;
            }

            emptyRowsCount++;
        }

        return emptyRowsCount > 0 ? this._map.slice(0, -emptyRowsCount) : this._map;
    }

    getStringifiedMap = () => {
        return this._getTrimmedMap().slice().reverse().map(row => row.join('')).join('\n');
    }

    getHeight = () => {
        // removing ground floor from map
        return this._getTrimmedMap().length - 1;
    }

    getSnapshots = () => {
        return Object
            .keys(this._snapshots)
            .sort((keyA, keyB) => {
                return this._snapshots[keyB].length - this._snapshots[keyA].length;
            })
            .map(snapshot => this._snapshots[snapshot]);
    }

    _isPeriodicSnapshot = (snapshot) => {
        const indexDiff = snapshot[1].rockIndex - snapshot[0].rockIndex;

        return snapshot.every((item, index) => {
            if (index === 0) {
                return true;
            }

            return item.rockIndex - snapshot[index - 1].rockIndex === indexDiff;
        })
    }

    getPeriodicSnapshots = () => {
        const snapshots = this.getSnapshots();

        return snapshots
            .filter(snapshot => snapshot.length > 1)
            .filter(this._isPeriodicSnapshot);
    }

    run() {
        for (let rockIndex = 0; rockIndex < this._rocksCount; rockIndex++) {
            this._runRock();
            if (this._ticksCount <= this._tickIndex) {
                return;
            }
            if (rockIndex % 1000 === 0) {
                console.log('rockIndex', rockIndex);
            }
        }
    }
}

module.exports = {
    Tetris
};
