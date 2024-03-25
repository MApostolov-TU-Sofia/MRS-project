from flask_sqlalchemy import SQLAlchemy
from api_application import app, db

class Transaction(db.Model):
    __tablename__ = 'mrst_transactions'

    id = db.Column(db.Integer, primary_key=True)
    bank_account_id = db.Column(db.Integer, nullable=False)
    process = db.Column(db.String(512), nullable=False)
    date = db.Column(db.Date, nullable=False)

    @property
    def serialize(self):
        return {
            'id': self.id,
            'bank_account_id': self.bank_account_id,
            'process': self.process,
            'date': self.date
        }
