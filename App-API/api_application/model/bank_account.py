from flask_sqlalchemy import SQLAlchemy
from api_application import app, db

class BankAccount(db.Model):
    __tablename__ = 'mrst_bank_accounts'

    id = db.Column(db.Integer, primary_key=True)
    bank_id = db.Column(db.Integer, nullable=False)
    user_id = db.Column(db.Integer, nullable=False)
    account_nbr = db.Column(db.String(128), nullable=False)
    cash = db.Column(db.Numeric(10, 2), nullable=False)

    @property
    def serialize(self):
        return {
            'id': self.id,
            'bank_id': self.bank_id,
            'user_id': self.user_id,
            'account_nbr': self.account_nbr,
            'cash': self.cash
        }