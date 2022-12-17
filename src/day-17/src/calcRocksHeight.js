const { Tetris } = require('./model/tetris');

const movesPattern = '>>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>';

const tetris = new Tetris({
    rocksCount: 27,
    movesPattern,
});

tetris.run();

console.log(tetris.getHeight());
