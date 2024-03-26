from flask_sqlalchemy import SQLAlchemy
from api_application import app, db

class Transaction(db.Model):
    __tablename__ = 'mrst_transactions'

    id = db.Column(db.Integer, primary_key=True)
    bank_account_id = db.Column(db.Integer, nullable=False)
    bank_account_iban = db.Column(db.String(128), nullable=False)
    to_bank_account_name = db.Column(db.String(128), nullable=False)
    to_bank_account_iban = db.Column(db.String(128), nullable=False)
    to_bank_account_cash = db.Column(db.Numeric(10, 2), nullable=False)
    to_bank_account_note = db.Column(db.String(128), nullable=False)
    to_bank_account_secondary_note = db.Column(db.String(128), nullable=True)
    process = db.Column(db.String(512), nullable=False)
    status = db.Column(db.Integer, nullable=False)
    date = db.Column(db.Date, nullable=False)

    @property
    def serialize(self):
        return {
            'id': self.id,
            'bank_account_id': self.bank_account_id,
            'bank_account_iban': self.bank_account_iban,
            'to_bank_account_name': self.to_bank_account_name,
            'to_bank_account_iban': self.to_bank_account_iban,
            'to_bank_account_cash': self.to_bank_account_cash,
            'to_bank_account_note': self.to_bank_account_note,
            'to_bank_account_secondary_note': self.to_bank_account_secondary_note,
            'process': self.process,
            'status': self.status,
            'date': self.date
        }
