from flask_sqlalchemy import SQLAlchemy
from api_application import app, db

class CreditCard(db.Model):
    __tablename__ = 'mrst_credit_cards'

    id = db.Column(db.Integer, primary_key=True)
    bank_account_id = db.Column(db.Integer, nullable=False)
    credit_card_nbr = db.Column(db.String(128), nullable=False)
    cvc_nbr = db.Column(db.Integer, nullable=False)
    limit = db.Column(db.Numeric(10, 2), nullable=False)
    valid_to = db.Column(db.Date, nullable=False)

    @property
    def serialize(self):
        return {
            'id': self.id,
            'bank_account_id': self.bank_account_id,
            'credit_card_nbr': self.credit_card_nbr,
            'limit': self.limit,
            'valid_to': self.valid_to
        }
