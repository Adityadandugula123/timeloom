const Session = require('../models/Session');
const Task = require('../models/Task');
const { calculateActiveDuration } = require('../algorithms/timeLoomAlgorithms');

exports.startSession = async (req, res, next) => {
  try {
    const { taskId, activityName, category } = req.body;
    const now = Date.now();
    const todayStr = new Date().toISOString().split('T')[0];

    const session = await Session.create({
      taskId,
      activityName: activityName || 'Focus Session',
      category: category || 'Study',
      startTime: now,
      endTime: now,
      durationSeconds: 0,
      pausedDurationMillis: 0,
      dateString: todayStr,
      status: 'Active'
    });

    res.status(201).json({ success: true, data: session });
  } catch (error) {
    next(error);
  }
};

exports.stopSession = async (req, res, next) => {
  try {
    const session = await Session.findById(req.params.id);
    if (!session) {
      return res.status(404).json({ success: false, message: 'Session not found' });
    }

    const endTime = Date.now();
    // Execute Algorithm 1: Duration calculation
    const durationSeconds = calculateActiveDuration(
      session.startTime,
      endTime,
      session.pausedDurationMillis
    );

    session.endTime = endTime;
    session.durationSeconds = durationSeconds;
    session.status = 'Completed';
    await session.save();

    // Increment task duration if associated
    if (session.taskId) {
      const minutes = Math.floor(durationSeconds / 60);
      if (minutes > 0) {
        await Task.findByIdAndUpdate(session.taskId, {
          $inc: { actualTrackedDuration: minutes }
        });
      }
    }

    res.status(200).json({ success: true, data: session });
  } catch (error) {
    next(error);
  }
};

exports.getSessions = async (req, res, next) => {
  try {
    const sessions = await Session.find().sort({ startTime: -1 });
    res.status(200).json({ success: true, count: sessions.length, data: sessions });
  } catch (error) {
    next(error);
  }
};
