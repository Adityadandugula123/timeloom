const mongoose = require('mongoose');

const SessionSchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: false
  },
  taskId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Task',
    required: false
  },
  activityName: {
    type: String,
    required: true,
    trim: true
  },
  category: {
    type: String,
    default: 'Study'
  },
  startTime: {
    type: Number, // epoch milliseconds
    required: true
  },
  endTime: {
    type: Number, // epoch milliseconds
    required: true
  },
  durationSeconds: {
    type: Number, // computed active duration
    required: true
  },
  pausedDurationMillis: {
    type: Number,
    default: 0
  },
  dateString: {
    type: String, // YYYY-MM-DD
    required: true
  },
  status: {
    type: String,
    enum: ['Active', 'Paused', 'Completed', 'Discarded'],
    default: 'Completed'
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = mongoose.model('Session', SessionSchema);
