const User = require('../models/User');

exports.register = async (req, res, next) => {
  try {
    const { name, email, password } = req.body;
    if (!name || !email || !password) {
      return res.status(400).json({ success: false, message: 'Please provide all required fields' });
    }

    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(409).json({ success: false, message: 'Email already registered' });
    }

    // Basic password hashing placeholder (e.g. bcrypt)
    const passwordHash = `hashed_${password}_salt2026`;

    const user = await User.create({
      name,
      email,
      passwordHash,
      dailyGoalMinutes: 120
    });

    res.status(201).json({
      success: true,
      data: {
        id: user._id,
        name: user.name,
        email: user.email,
        dailyGoalMinutes: user.dailyGoalMinutes
      }
    });
  } catch (error) {
    next(error);
  }
};

exports.login = async (req, res, next) => {
  try {
    const { email, password } = req.body;
    const user = await User.findOne({ email });
    if (!user) {
      return res.status(401).json({ success: false, message: 'Invalid credentials' });
    }

    res.status(200).json({
      success: true,
      message: 'Login successful',
      token: 'jwt_academic_session_token_timeloom',
      user: {
        id: user._id,
        name: user.name,
        email: user.email,
        dailyGoalMinutes: user.dailyGoalMinutes
      }
    });
  } catch (error) {
    next(error);
  }
};
