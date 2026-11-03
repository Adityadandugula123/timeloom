const express = require('express');
const router = express.Router();
const {
  startSession,
  stopSession,
  getSessions
} = require('../controllers/sessionController');

router.post('/start', startSession);
router.patch('/:id/stop', stopSession);
router.get('/', getSessions);

module.exports = router;
