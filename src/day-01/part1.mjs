import { readToString } from "./stdin.mjs";

async function solve() {
  const input = await readToString().then(res => res
    .trim()
    .split("\n")
    .map(i => parseInt(i, 10)));

  let currentElfCalories = 0;
  let maxElfCalories = 0;
    
  for (const calory of input) {
    if (Number.isInteger(calory)) {
      currentElfCalories += calory;
    } else {
      maxElfCalories = Math.max(currentElfCalories, maxElfCalories);
      currentElfCalories = 0;
    }
  }

  return maxElfCalories;
 
}

solve().then(console.log);