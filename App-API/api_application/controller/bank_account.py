import json
from flask_bcrypt import Bcrypt as bcrypt
import swiftcrypt
from datetime import datetime
from flask import render_template, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import text, desc
from api_application import app, db
from api_application.model import bank as bank_db
from api_application.model import bank_account as bank_account_db
from api_application.model import user as user_db
from api_application.model import transaction as transaction_db
from decimal import Decimal


def view(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.role_id == 1):
        db_bak_account_check = bank_account_db.BankAccount.query.filter_by(id=args.get("bank_account_id")).first()
        responseJSON['data'] = db_bak_account_check.serialize
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Bank account is not found or you do not have rights to review'

    return jsonify(responseJSON)

def view_all(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.role_id == 1):
        db_bank_account_check = bank_account_db.BankAccount.query\
            .order_by(desc(bank_account_db.BankAccount.id))\
            .all()
        responseJSON['data'] = [d.serialize for d in db_bank_account_check]
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Bank account is not found or you do not have rights to review'

    return jsonify(responseJSON)

def view_by(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.salt == args.get('token')):
        db_bank_account_check = bank_account_db.BankAccount.query\
            .filter_by(user_id=args.get('user_id'))\
            .order_by(desc(bank_account_db.BankAccount.id))\
            .all()
        responseJSON['status'] = 'success'
        responseJSON['data'] = [d.serialize for d in db_bank_account_check]
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Bank account is not found or you do not have rights to review'
    return jsonify(responseJSON)

def create(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None):
        bank_db_txt = bank_db.Bank.query.filter_by(id=int(args.get('bank_id'))).first()
        bank_account_db_count = bank_account_db.BankAccount.query.count()
        bank_account_db_count += 1
        bank_account_code = 'BG80' + bank_db_txt.swift + str(bank_account_db_count).zfill(14)

        new_record = bank_account_db.BankAccount()
        new_record.bank_id = args.get('bank_id')
        new_record.user_id = args.get('user_id')
        new_record.account_nbr = bank_account_code
        new_record.status = 1
        new_record.cash = 0.0

        db.session.add(new_record)
        db.session.commit()
        db_bank_account_check = bank_account_db.BankAccount.query\
            .filter_by(user_id=args.get('user_id'))\
            .order_by(desc(bank_account_db.BankAccount.id))\
            .all()
        responseJSON['status'] = 'success'
        responseJSON['message'] = 'Bank account is added'
        responseJSON['data'] = [d.serialize for d in db_bank_account_check]
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Bank account is not found or you do not have rights to review'

    return jsonify(responseJSON)


def modify(args):
    responseJSON = {
        'status': None,
        'message': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and (db_user_check.role_id == 1 or
                db_user_check.salt == args.get('token'))):
        db_bank_account_to_update = bank_account_db.BankAccount.query.filter_by(id=args.get('bank_account_id')).first()
        if (db_bank_account_to_update is not None):
            if (args.get('action') == 'update'):
                db_bank_account_to_update.cash = Decimal(args.get('new_cash'))
                new_record = transaction_db.Transaction()
                new_record.bank_account_id = args.get('bank_account_id')
                new_record.process = args.get('process')
                new_record.date = datetime.now()

                db.session.add(new_record)
                db.session.commit()

                responseJSON['status'] = 'success'
                responseJSON['message'] = 'Successful update'
            elif (args.get('action') == 'delete'):
                db.session.remove(db_bank_account_to_update)
                new_record = transaction_db.Transaction()
                new_record.bank_account_id = args.get('bank_account_id')
                new_record.process = args.get('process')
                new_record.date = datetime.now()

                db.session.add(new_record)
                db.session.commit()
                responseJSON['status'] = 'success'
                responseJSON['message'] = 'Successful delete'
            else:
                responseJSON['status'] = 'fail'
                responseJSON['message'] = 'No action is selected'
        else:
            responseJSON['status'] = 'fail'
            responseJSON['message'] = 'No bank account is found'

    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Bank account is not found or you do not have rights to review'

    return jsonify(responseJSON)
