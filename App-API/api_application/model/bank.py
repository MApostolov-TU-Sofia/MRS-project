from flask_sqlalchemy import SQLAlchemy
from api_application import app, db

class Bank(db.Model):
    __tablename__ = 'mrst_banks'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(256), nullable=False)
    swift = db.Column(db.String(256), nullable=False)
    address = db.Column(db.String(256), nullable=False)
    description = db.Column(db.Text, nullable=False)

    @property
    def serialize(self):
        return {
            'id': self.id,
            'name': self.name,
            'swift': self.swift,
            'address': self.address,
            'description': self.description
        }