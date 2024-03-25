from flask_sqlalchemy import SQLAlchemy
from api_application import app, db

class User(db.Model):
    __tablename__ = 'mrst_users'

    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(256), unique=True, nullable=False)
    password = db.Column(db.String(256), nullable=False)
    salt = db.Column(db.String(128), nullable=False)
    bank_id = db.Column(db.Integer, nullable=False)
    role_id = db.Column(db.Integer, nullable=False)
    name = db.Column(db.String(512), nullable=True)
    address = db.Column(db.String(256), nullable=True)
    phone_nbr = db.Column(db.String(32), nullable=True)
    job = db.Column(db.String(128), nullable=True)

    @property
    def serialize(self):
        return {
            'id': self.id,
            'username': self.username,
            'password': self.password,
            'salt': self.salt,
            'bank_id': self.bank_id,
            'role_id': self.role_id,
            'name': self.name,
            'address': self.address,
            'phone_nbr': self.phone_nbr,
            'job': self.job
        }
