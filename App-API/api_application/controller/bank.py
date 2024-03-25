import json
from flask_bcrypt import Bcrypt as bcrypt
import swiftcrypt
from datetime import datetime
from flask import render_template, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import text
from api_application import app, db
from api_application.model import bank as bank_db
from api_application.model import user as user_db


def view(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.role_id == 1):
        db_role_check = bank_db.Bank.query.filter_by(name=args.get("name")).first()
        responseJSON['data'] = db_role_check.serialize
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Bank is not found or you do not have rights to review'

    return jsonify(responseJSON)

def view_all(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.role_id == 1):
        db_role_check = bank_db.Bank.query.all()
        responseJSON['data'] = [d.serialize for d in db_role_check]
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Bank is not found or you do not have rights to review'

    return jsonify(responseJSON)

def create(args):
    responseJSON = {
        'status': None,
        'message': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.role_id == 1):
        new_record = bank_db.Bank()
        new_record.name = args.get('name')
        new_record.address = args.get('address')
        db.session.add(new_record)
        db.session.commit()
        responseJSON['status'] = 'success'
        responseJSON['message'] = 'Bank is added'
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'User is missing or has no rights'

    return jsonify(responseJSON)


def modify(args):
    responseJSON = {
        'status': None,
        'message': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.role_id == 1):
        db_bank_to_update = bank_db.Bank.query.filter_by(name=args.get('name')).first()
        if (args.get('action') == 'update'):
            db_bank_to_update.name = args.get('new_name')
            db_bank_to_update.address = args.get('new_address')
            db.session.commit()
            responseJSON['status'] = 'success'
            responseJSON['message'] = 'Successful update'
        elif (args.get('action') == 'delete'):
            db.session.remove(db_bank_to_update)
            db.session.commit()
            responseJSON['status'] = 'success'
            responseJSON['message'] = 'Successful delete'
        else:
            responseJSON['status'] = 'fail'
            responseJSON['message'] = 'No action is selected'
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Bank is not found or you do not have rights to review'

    return jsonify(responseJSON)

def init():
    db_bank_to_update = bank_db.Bank.query.filter_by(name='BANK').first()
    if (db_bank_to_update == None):
        new_record = bank_db.Bank()
        new_record.name = 'BANK'
        new_record.swift = 'BGSF'
        new_record.address = ''
        db.session.add(new_record)
        db.session.commit()
    print('Init banks checked')