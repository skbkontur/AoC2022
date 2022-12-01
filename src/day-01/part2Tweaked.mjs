import { readToString } from "./stdin.mjs";

const TOP_ELVES_COUNT = 3;

async function solve() {
  const input = await readToString().then(res => res
    .trim()
    .split("\n")
    .map(i => parseInt(i, 10)));

  let caloriesForElf = [];
  let currentElf = 0;
    
  for (const calory of input) {
    if (Number.isInteger(calory)) {
      if (!caloriesForElf[currentElf]) {
        caloriesForElf[currentElf] = 0;
      }
      caloriesForElf[currentElf] += calory;
    } else {
      currentElf++;
    }
  }

  caloriesForElf.sort((a, b) => b - a);

  return caloriesForElf.slice(0, TOP_ELVES_COUNT).reduce((sum, cur) => sum + cur, 0);
 
}

solve().then(console.log);