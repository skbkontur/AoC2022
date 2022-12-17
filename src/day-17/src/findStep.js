const { Tetris } = require('./model/tetris');

const movesPattern = '>>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>';

for (let i = 1; i < 40; i++) {
    let prevDiff = 0;
    let prevHeight = 0;
    let prevMapSlice = '';
    for (let j = 1; j < 5; j++) {
        const tetris = new Tetris({
            rocksCount: i * j,
            movesPattern,
        });

        tetris.run();

        const map = tetris.getStringifiedMap();

        const mapSlice = map.split('\n').slice(0, 10).join('\n');
        const height = tetris.getHeight();
        const diff = height - prevHeight;

        if (mapSlice === prevMapSlice) {
            console.log(i, 'maps are equal', 'height diff:', diff);
            console.log('----');
        }

        prevHeight = height;
        prevDiff = diff;
        prevMapSlice = mapSlice;

    }
    console.log('-----------next step--------');

}
