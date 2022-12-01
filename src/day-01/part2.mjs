import { readToString } from "./stdin.mjs";

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

  return caloriesForElf[0] + caloriesForElf[1] + caloriesForElf[2];
 
}

solve().then(console.log);