const Session = require('../models/Session');
const Task = require('../models/Task');
const {
  aggregateDailyMinutes,
  generateMonthHeatmap,
  generateWeeklyReport,
  calculateProductivityScore
} = require('../algorithms/timeLoomAlgorithms');

exports.getDailyReport = async (req, res, next) => {
  try {
    const sessions = await Session.find({ status: 'Completed' });
    const aggregation = aggregateDailyMinutes(sessions);
    res.status(200).json({ success: true, data: aggregation });
  } catch (error) {
    next(error);
  }
};

exports.getWeeklyReport = async (req, res, next) => {
  try {
    const sessions = await Session.find({ status: 'Completed' });
    const tasks = await Task.find();
    const report = generateWeeklyReport(sessions, tasks, new Date());
    res.status(200).json({ success: true, data: report });
  } catch (error) {
    next(error);
  }
};

exports.getHeatmapReport = async (req, res, next) => {
  try {
    const now = new Date();
    const year = parseInt(req.query.year) || now.getFullYear();
    const month = parseInt(req.query.month) || (now.getMonth() + 1);

    const sessions = await Session.find({ status: 'Completed' });
    const heatmap = generateMonthHeatmap(sessions, year, month);

    res.status(200).json({ success: true, year, month, data: heatmap });
  } catch (error) {
    next(error);
  }
};

exports.getProductivityScore = async (req, res, next) => {
  try {
    const sessions = await Session.find({ status: 'Completed' });
    const tasks = await Task.find();
    const dailyGoalMinutes = parseInt(req.query.dailyGoal) || 120;

    const scoreResult = calculateProductivityScore(tasks, sessions, dailyGoalMinutes);
    res.status(200).json({ success: true, data: scoreResult });
  } catch (error) {
    next(error);
  }
};
