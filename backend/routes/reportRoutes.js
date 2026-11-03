const express = require('express');
const router = express.Router();
const {
  getDailyReport,
  getWeeklyReport,
  getHeatmapReport,
  getProductivityScore
} = require('../controllers/reportController');

router.get('/daily', getDailyReport);
router.get('/weekly', getWeeklyReport);
router.get('/heatmap', getHeatmapReport);
router.get('/productivity', getProductivityScore);

module.exports = router;
