const express = require("express");
const cors = require("cors");
const dotenv = require("dotenv");
const { AccessToken } = require("livekit-server-sdk");

dotenv.config();

const app = express();

app.use(cors());
app.use(express.json());

app.get("/", (req, res) => {
    res.json({
        success: true,
        message: "EmaTalk LiveKit Backend is running"
    });
});

app.get("/token", async (req, res) => {
    try {
        const room = req.query.room;
        const identity = req.query.identity;

        if (!room || !identity) {
            return res.status(400).json({
                error: "room and identity are required"
            });
        }

        const token = new AccessToken(
            process.env.LIVEKIT_API_KEY,
            process.env.LIVEKIT_API_SECRET,
            {
                identity: identity,
                name: identity
            }
        );

        token.addGrant({
            roomJoin: true,
            room: room,
            canPublish: true,
            canSubscribe: true,
            canPublishData: true
        });

        const jwt = await token.toJwt();

        res.json({
            token: jwt,
            url: process.env.LIVEKIT_URL
        });

    } catch (error) {
        console.error(error);

        res.status(500).json({
            error: "Token generation failed"
        });
    }
});

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
    console.log(`EmaTalk backend running on port ${PORT}`);
});