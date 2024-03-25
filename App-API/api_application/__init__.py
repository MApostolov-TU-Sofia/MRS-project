from flask_sqlalchemy import SQLAlchemy
from flask import Flask

DATABASE_URL = 'mysql+mysqlconnector://tu_sofia_mrs:tu_sofia_mrs_psw@localhost:3306/tu_sofia_mrs_bank_app'

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = DATABASE_URL
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

import api_application.model.role
import api_application.model.user
import api_application.model.bank
import api_application.model.bank_account
import api_application.model.credit_card
import api_application.model.transaction

import api_application.controller.home
import api_application.controller.role
import api_application.controller.user
import api_application.controller.bank
import api_application.controller.bank_account
import api_application.controller.credit_card
import api_application.controller.transaction

import api_application.routes.home
import api_application.routes.role
import api_application.routes.user
import api_application.routes.bank
import api_application.routes.bank_account
import api_application.routes.credit_card
import api_application.routes.transaction

with app.app_context():
    db.create_all()
    api_application.controller.role.init()
    api_application.controller.bank.init()