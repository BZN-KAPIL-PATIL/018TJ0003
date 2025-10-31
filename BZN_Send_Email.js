const nodemailer = require('nodemailer');
const fs = require('fs');

async function main() {
  const transporter = nodemailer.createTransport({
    host: process.env.SMTP_SERVER,
    port: parseInt(process.env.SMTP_PORT),
    secure: process.env.SMTP_PORT === '465',
    name: process.env.HELO_HOST || 'rediffmailpro.com',
    auth: {
      user: process.env.SMTP_USERNAME,
      pass: process.env.SMTP_PASSWORD
    },
    tls: {
      rejectUnauthorized: process.env.IGNORE_CERT !== 'true'
    }
  });

  await transporter.sendMail({
    from: process.env.EMAIL_FROM,
    to: process.env.EMAIL_TO,
    cc: process.env.EMAIL_CC,
    subject: process.env.EMAIL_SUBJECT,
    text: process.env.EMAIL_BODY,
    attachments: [
      { filename: 'New_Released.zip', content: fs.readFileSync('New_Released.zip') }
    ]
  });

  console.log('✔️ Email sent successfully');
}

main().catch(err => {
  console.error('❌ Failed to send email', err);
  process.exit(1);
});
