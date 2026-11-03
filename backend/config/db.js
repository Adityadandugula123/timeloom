/**
 * MongoDB Connection Handler using Mongoose
 * Academic B.Tech CSE Project - Time Loom
 */

const mongoose = require('mongoose');

const connectDB = async () => {
  try {
    const mongoURI = process.env.MONGO_URI || 'mongodb://127.0.0.1:27017/timeloom_db';
    const conn = await mongoose.connect(mongoURI, {
      serverSelectionTimeoutMS: 5000
    });
    console.log(`[MongoDB Connected]: ${conn.connection.host}`);
  } catch (error) {
    console.warn(`[MongoDB Warning]: Could not connect to MongoDB server: ${error.message}`);
    console.warn('[Notice]: For faculty demo, run local MongoDB or provide MONGO_URI in .env');
  }
};

module.exports = connectDB;
