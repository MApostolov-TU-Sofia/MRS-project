from flask_sqlalchemy import SQLAlchemy
from api_application import app, db

class Role(db.Model):
    __tablename__ = 'mrst_roles'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(32), nullable=False)

    @property
    def serialize(self):
        return {
            'id': self.id,
            'name': self.name
        }
