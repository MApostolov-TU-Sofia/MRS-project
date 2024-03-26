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
    if (db_user_check is not None and db_user_check.salt == args.get('token')):
        bank_account_check_db = bank_account_db.BankAccount.query.filter_by(id=args.get("bank_account_id")).first()
        if (bank_account_check_db is not None):
            transfer_tax = '0.0'
            if (args.get('process') == 'TRANSFER'):
                transfer_tax = '1.5'
            elif (args.get('process') == 'INTERNAL_TRANSFER'):
                transfer_tax = '0.0'
            after_transaction_amount = bank_account_check_db.cash - Decimal(args.get('to_bank_account_cash')) - Decimal(transfer_tax)
            if (after_transaction_amount > 0.0):
                bank_account_check_db.cash = after_transaction_amount
                db.session.commit()

                to_bank_account_check_db = bank_account_db.BankAccount.query\
                    .filter_by(account_nbr=args.get('to_bank_account_iban'))\
                    .first()

                if (to_bank_account_check_db is not None):
                    to_bank_account_check_db.cash = to_bank_account_check_db.cash + Decimal(args.get('to_bank_account_cash'))
                    db.session.commit()

                new_record = transaction_db.Transaction()
                new_record.bank_account_id = int(args.get('bank_account_id'))
                new_record.bank_account_iban = args.get('bank_account_iban')
                new_record.to_bank_account_name = args.get('to_bank_account_name')
                new_record.to_bank_account_iban = args.get('to_bank_account_iban')
                new_record.to_bank_account_cash = Decimal(args.get('to_bank_account_cash'))
                new_record.to_bank_account_note = args.get('to_bank_account_note')
                new_record.to_bank_account_secondary_note = args.get('to_bank_account_secondary_note')
                new_record.process = args.get('process')
                new_record.status = 1
                new_record.date = datetime.now()

                db.session.add(new_record)
                db.session.commit()
                if (args.get('process') == 'TRANSFER'):
                    new_record_tax = transaction_db.Transaction()
                    new_record_tax.bank_account_id = int(args.get('bank_account_id'))
                    new_record_tax.bank_account_iban = args.get('bank_account_iban')
                    new_record_tax.to_bank_account_name = 'BANK TAX'
                    new_record_tax.to_bank_account_iban = 'BANK TAX'
                    new_record_tax.to_bank_account_cash = Decimal(transfer_tax)
                    new_record_tax.to_bank_account_note = 'BANK TAX'
                    new_record_tax.to_bank_account_secondary_note = ''
                    new_record_tax.process = 'BANK TAX'
                    new_record_tax.status = 1
                    new_record_tax.date = datetime.now()

                    db.session.add(new_record_tax)
                    db.session.commit()

                responseJSON['status'] = 'success'
                responseJSON['message'] = 'Transaction is added'
            else:
                responseJSON['status'] = 'fail'
                responseJSON['message'] = 'Not enough amount in bank account'
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
