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
        db_transaction_check = transaction_db.Transaction.query.filter_by(id=args.get("bank_account_id")).first()
        responseJSON['data'] = db_transaction_check.serialize
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Transaction is not found or you do not have rights to review'

    return jsonify(responseJSON)

def view_all(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.role_id == 1):
        db_transactions_check = transaction_db.Transaction.query \
            .filter_by(id=args.get("bank_account_id"))\
            .order_by(desc(transaction_db.Transaction.id))\
            .all()
        responseJSON['data'] = [d.serialize for d in db_transactions_check]
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Transaction is not found or you do not have rights to review'

    return jsonify(responseJSON)

def view_by(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.salt == args.get('token')):
        db_transaction_check = transaction_db.Transaction.query\
            .filter_by(bank_account_id=args.get('bank_account_id'))\
            .order_by(desc(transaction_db.Transaction.id))\
            .all()
        responseJSON['status'] = 'success'
        responseJSON['data'] = [d.serialize for d in db_transaction_check]
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Transaction is not found or you do not have rights to review'
    return jsonify(responseJSON)

def create(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None):
        new_record = transaction_db.Transaction()
        new_record.bank_account_id = args.get('bank_account_id')
        new_record.process = args.get('process')
        new_record.date = datetime.now()

        db.session.add(new_record)
        db.session.commit()
        db_bank_account_check = transaction_db.Transaction.query\
            .filter_by(bank_account_id=args.get('bank_account_id'))\
            .order_by(desc(transaction_db.Transaction.id))\
            .all()
        responseJSON['status'] = 'success'
        responseJSON['message'] = 'Transaction is added'
        responseJSON['data'] = [d.serialize for d in db_bank_account_check]
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Transaction is not found or you do not have rights to review'

    return jsonify(responseJSON)


def modify(args):
    responseJSON = {
        'status': None,
        'message': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and (db_user_check.role_id == 1 or
                db_user_check.salt == args.get('token'))):
        db_transaction_update = transaction_db.Transaction.query.filter_by(id=args.get('id')).first()
        if (db_transaction_update is not None):
            if (args.get('action') == 'update'):
                db_transaction_update.process = args.get('process')
                db_transaction_update.date = datetime.now()
                db_transaction_update.update(dict(db_transaction_update))
                db.session.commit()

                responseJSON['status'] = 'success'
                responseJSON['message'] = 'Successful update'
            elif (args.get('action') == 'delete'):
                db.session.remove(db_transaction_update)
                db.session.commit()
                responseJSON['status'] = 'success'
                responseJSON['message'] = 'Successful delete'
            else:
                responseJSON['status'] = 'fail'
                responseJSON['message'] = 'No action is selected'
        else:
            responseJSON['status'] = 'fail'
            responseJSON['message'] = 'No transaction is found'

    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Transaction is not found or you do not have rights to review'

    return jsonify(responseJSON)
