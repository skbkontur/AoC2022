// @ts-check

const example = `\
Blueprint 1: Each ore robot costs 4 ore.\
 Each clay robot costs 2 ore.\
 Each obsidian robot costs 3 ore and 14 clay.\
 Each geode robot costs 2 ore and 7 obsidian.\

Blueprint 2: Each ore robot costs 2 ore.\
 Each clay robot costs 3 ore.\
 Each obsidian robot costs 3 ore and 8 clay.\
 Each geode robot costs 3 ore and 12 obsidian.
`;

const lineTpl = tpl`\
Blueprint ${"index|int"}:\
 Each ore robot costs ${"oreBotOre|int"} ore.\
 Each clay robot costs ${"clayBotOre|int"} ore.\
 Each obsidian robot costs ${"obsidianBotOre|int"} ore and ${"obsidianBotClay|int"} clay.\
 Each geode robot costs ${"geodeBotOre|int"} ore and ${"geodeBotObsidian|int"} obsidian.\
`;

/**
 *
 * @param {ReturnType<typeof lineTpl>} bp
 * @param {number} timeLeft
 */
function countGeodes(bp, timeLeft) {
  const maxOreRobots = Math.max(
    bp.oreBotOre,
    bp.clayBotOre,
    bp.obsidianBotOre,
    bp.geodeBotOre
  );
  const maxClayRobots = bp.obsidianBotClay;
  const maxObsidianRobots = bp.geodeBotObsidian;

  /**
   * @param {number} oreBots
   * @param {number} clayBots
   * @param {number} obsidianBots
   * @param {number} geodeBots
   * @param {number} ore
   * @param {number} clay
   * @param {number} obsidian
   * @param {number} geode
   * @param {number} timePassed
   * @param {number} totalTime
   */
  function dfs(
    oreBots,
    clayBots,
    obsidianBots,
    geodeBots,
    ore,
    clay,
    obsidian,
    geode,
    timePassed,
    totalTime
  ) {
    let isOreRobotBuilt = false;
    let isClayRobotBuilt = false;
    let isObsidianRobotBuilt = false;
    let isGeodeRobotBuilt = false;

    let score = geode;

    for (let i = timePassed; i < totalTime; i++) {
      const canBuildOreRobot = bp.oreBotOre <= ore;
      const canBuildClayRobot = bp.clayBotOre <= ore;
      const canBuildObsidianRobot =
        bp.obsidianBotOre <= ore && bp.obsidianBotClay <= clay;
      const canBuildGeodeRobot =
        bp.geodeBotOre <= ore && bp.geodeBotObsidian <= obsidian;

      ore += oreBots;
      clay += clayBots;
      obsidian += obsidianBots;
      geode += geodeBots;

      if (canBuildGeodeRobot && !isGeodeRobotBuilt) {
        const result = dfs(
          oreBots,
          clayBots,
          obsidianBots,
          geodeBots + 1,
          ore - bp.geodeBotOre,
          clay,
          obsidian - bp.geodeBotObsidian,
          geode,
          i + 1,
          totalTime
        );
        score = Math.max(score, result);
        isGeodeRobotBuilt = canBuildGeodeRobot;
      }

      if (
        canBuildObsidianRobot &&
        !isObsidianRobotBuilt &&
        obsidianBots < maxObsidianRobots
      ) {
        const result = dfs(
          oreBots,
          clayBots,
          obsidianBots + 1,
          geodeBots,
          ore - bp.obsidianBotOre,
          clay - bp.obsidianBotClay,
          obsidian,
          geode,
          i + 1,
          totalTime
        );
        score = Math.max(score, result);
        isObsidianRobotBuilt = canBuildObsidianRobot;
      }

      if (canBuildClayRobot && !isClayRobotBuilt && clayBots < maxClayRobots) {
        const result = dfs(
          oreBots,
          clayBots + 1,
          obsidianBots,
          geodeBots,
          ore - bp.clayBotOre,
          clay,
          obsidian,
          geode,
          i + 1,
          totalTime
        );
        score = Math.max(score, result);
        isClayRobotBuilt = canBuildClayRobot;
      }

      if (canBuildOreRobot && !isOreRobotBuilt && oreBots < maxOreRobots) {
        const result = dfs(
          oreBots + 1,
          clayBots,
          obsidianBots,
          geodeBots,
          ore - bp.oreBotOre,
          clay,
          obsidian,
          geode,
          i + 1,
          totalTime
        );
        score = Math.max(score, result);
        isOreRobotBuilt = canBuildOreRobot;
      }

      score = Math.max(score, geode);
    }
    return score;
  }

  return dfs(1, 0, 0, 0, 0, 0, 0, 0, 0, timeLeft);
}

/**
 * @param {string} input
 */
function part1(input) {
  const blueprints = input.trim().split("\n").map(lineTpl);

  return blueprints
    .map((x) => countGeodes(x, 24))
    .map((value, index) => (index + 1) * value)
    .reduce((a, b) => a + b, 0);
}

/**
 * @param {string} input
 */
function part2(input) {
  const blueprints = input.trim().split("\n").map(lineTpl);

  return blueprints
    .slice(0, 3)
    .map((x) => countGeodes(x, 32))
    .reduce((a, b) => a * b, 1);
}
