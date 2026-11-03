/**
 * TIME LOOM - Backend Server Entrypoint
 * Academic B.Tech CSE Project: Personal Time-Tracking & Analytics
 * 
 * Technology: Node.js, Express.js, MongoDB (Mongoose)
 */

const express = require('express');
const cors = require('cors');
require('dotenv').config();

const connectDB = require('./config/db');

// Route Handlers
const authRoutes = require('./routes/authRoutes');
const taskRoutes = require('./routes/taskRoutes');
const sessionRoutes = require('./routes/sessionRoutes');
const reportRoutes = require('./routes/reportRoutes');

const app = express();

// Standard Middlewares
app.use(cors());
app.use(express.json());

// Initialize Database Connection
connectDB();

// API Routes
app.use('/api/auth', authRoutes);
app.use('/api/tasks', taskRoutes);
app.use('/api/sessions', sessionRoutes);
app.use('/api/reports', reportRoutes);

// Base Health Check
app.get('/api/health', (req, res) => {
  res.status(200).json({
    status: 'ONLINE',
    project: 'TIME LOOM',
    tagline: 'Track. Understand. Improve.',
    academicYear: '2026',
    timestamp: new Date().toISOString()
  });
});

// Centralized Error Handling Middleware
app.use((err, req, res, next) => {
  console.error('[Error Handler]:', err.stack);
  res.status(err.status || 500).json({
    success: false,
    message: err.message || 'Internal Server Error'
  });
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
  console.log(`Time Loom REST API listening on port ${PORT}`);
});

module.exports = app;
