import json
from flask_bcrypt import Bcrypt as bcrypt
import swiftcrypt
from datetime import datetime, timedelta
from flask import render_template, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import text
from api_application import app, db
from api_application.model import bank as bank_db
from api_application.model import bank_account as bank_account_db
from api_application.model import credit_card as credit_card_db
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
    if (db_user_check is not None and (db_user_check.role_id == 1 or
            db_user_check.salt == args.get('token'))):
        db_credit_card_check = credit_card_db.CreditCard.query.filter_by(id=args.get("credit_card_id")).first()
        responseJSON['data'] = db_credit_card_check.serialize
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Credit card is not found or you do not have rights to review'

    return jsonify(responseJSON)

def view_all(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.role_id == 1):
        db_credit_card_check = credit_card_db.CreditCard.query.all()
        responseJSON['data'] = [d.serialize for d in db_credit_card_check]
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Credit card is not found or you do not have rights to review'

    return jsonify(responseJSON)

def view_by(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.salt == args.get('token')):
        db_credit_card_check = credit_card_db.CreditCard.query.filter_by(bank_account_id=args.get('credit_card_id')).all()
        responseJSON['data'] = [d.serialize for d in db_credit_card_check]
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Credit card is not found or you do not have rights to review'

    return jsonify(responseJSON)

def create(args):
    responseJSON = {
        'status': None,
        'message': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.salt == args.get('token')):
        bank_db_txt = bank_db.Bank.query.filter_by(id=args.get('bank_account_id')).first()

        bank_account_db_count = bank_account_db.BankAccount.query.count()
        bank_account_db_count += 1
        bank_account_code = 'BG80' + bank_db_txt.swift + str(bank_account_db_count).zfill(14)

        new_record = credit_card_db.CreditCard()
        new_record.bank_account_id = args.get('bank_account_id')
        new_record.credit_card_nbr = bank_account_code
        new_record.limit = args.get('limit')
        new_record.valid_to = datetime.now() + timedelta(days=(365 * 2))

        db.session.add(new_record)
        db.session.commit()
        responseJSON['status'] = 'success'
        responseJSON['message'] = 'Credit card is added'
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'You do not have rights to review'

    return jsonify(responseJSON)


def modify(args):
    responseJSON = {
        'status': None,
        'message': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and (db_user_check.role_id == 1 or
                db_user_check.salt == args.get('token'))):
        db_credit_card_to_update = credit_card_db.CreditCard.query.filter_by(id=args.get('credit_card_id')).first()
        if (db_credit_card_to_update is not None):
            if (args.get('action') == 'update'):
                db_credit_card_to_update.limit = Decimal(args.get('new_limit'))
                db.session.commit()

                responseJSON['status'] = 'success'
                responseJSON['message'] = 'Successful update'
            elif (args.get('action') == 'delete'):
                db.session.remove(db_credit_card_to_update)
                db.session.commit()
                responseJSON['status'] = 'success'
                responseJSON['message'] = 'Successful delete'
            else:
                responseJSON['status'] = 'fail'
                responseJSON['message'] = 'No action is selected'
        else:
            responseJSON['status'] = 'fail'
            responseJSON['message'] = 'No credit card is found'

    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Credit card is not found or you do not have rights to review'

    return jsonify(responseJSON)
