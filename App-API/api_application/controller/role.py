import json
from flask_bcrypt import Bcrypt as bcrypt
import swiftcrypt
from datetime import datetime
from flask import render_template, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import text
from api_application import app, db
from api_application.model import role as role_db
from api_application.model import user as user_db


def view(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.role_id == 1):
        db_role_check = role_db.Role.query.filter_by(name=args.get("name")).first()
        responseJSON['data'] = db_role_check.serialize
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Role is not found or you do not have rights to review'

    return jsonify(responseJSON)

def view_all(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.role_id == 1):
        db_role_check = role_db.Role.query.all()
        responseJSON['data'] = [d.serialize for d in db_role_check]
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Role is not found or you do not have rights to review'

    return jsonify(responseJSON)

def create(args):
    responseJSON = {
        'status': None,
        'message': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.role_id == 1):
        new_record = role_db.Role()
        new_record.name = args.get('name')
        db.session.add(new_record)
        db.session.commit()
        responseJSON['status'] = 'success'
        responseJSON['message'] = 'Role is added'
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Role is not found or you do not have rights to review'

    return jsonify(responseJSON)


def modify(args):
    responseJSON = {
        'status': None,
        'message': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.role_id == 1):
        db_role_to_update = role_db.Role.query.filter_by(name=args.get('name')).first()
        if (args.get('action') == 'update'):
            db_role_to_update.name = args.get('new_name')
            db.session.commit()
            responseJSON['status'] = 'success'
            responseJSON['message'] = 'Successful update'
        elif (args.get('action') == 'delete'):
            db.session.remove(db_role_to_update)
            db.session.commit()
            responseJSON['status'] = 'success'
            responseJSON['message'] = 'Successful delete'
        else:
            responseJSON['status'] = 'fail'
            responseJSON['message'] = 'No action is selected'
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'Role is not found or you do not have rights to review'

    return jsonify(responseJSON)

def init():
    db_role_to_update_adm = role_db.Role.query.filter_by(name='ADM').first()
    db_role_to_update_std = role_db.Role.query.filter_by(name='STD').first()
    if (db_role_to_update_adm == None):
        new_record = role_db.Role()
        new_record.name = 'ADM'
        db.session.add(new_record)
        db.session.commit()
    if (db_role_to_update_std == None):
        new_record = role_db.Role()
        new_record.name = 'STD'
        db.session.add(new_record)
        db.session.commit()
    print('Init roles checked')