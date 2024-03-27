import json
from flask_bcrypt import Bcrypt as bcrypt
import swiftcrypt
from datetime import datetime
from flask import render_template, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import text
from api_application import app, db
from api_application.model import user as user_db

def JSONifyFunc(obj):
    iObj = []
    i = 0
    for x in obj:
        iObj.insert(i, {
            'id': x[0],
            'username': x[1],
            'password': x[2],
            'salt': x[3],
            'pin': x[4],
            'bank_id': x[5],
            'role_id': x[6],
            'name': x[7],
            'address': x[8],
            'phone_nbr': x[9],
            'job': x[10]
        })
        i += 1
    return iObj

def login(args):
    responseJSON = {
        'status': None,
        'message': None,
        'token': None,
        'username': None
    }
    # with db.engine.connect() as conn:
    #     resp = conn\
    #         .execute(text(f'SELECT id, username, password, salt, bank_id, role_id, name, address, phone_nbr, job '
    #                       f'  FROM tu_sofia_mrs_bank_app.mrst_users '
    #                       f' WHERE username = "{args.get("username")}" '
    #                       f'   AND password = "{args.get("password")}"'))\
    #         .all()
    db_user_check = user_db.User.query.filter_by(username=args.get("username")).first()

    # respJSON = JSONifyFunc(resp)
    if (db_user_check is not None):
        hashedPass = swiftcrypt.Hash().hash_password(args.get("password"), db_user_check.salt, "sha512")
        if (hashedPass == db_user_check.password and int(args.get("pin")) == db_user_check.pin):
            salt = swiftcrypt.Salts().generate_salt(16).encode('utf-8').hex()
            hashedPass = swiftcrypt.Hash().hash_password(args.get("password"), salt, "sha512")
            db_user_check.salt = salt
            db_user_check.password = hashedPass
            db.session.commit()
            responseJSON['status'] = 'success'
            responseJSON['message'] = 'Successful login'
            responseJSON['token'] = salt
            responseJSON['username'] = args.get("username")
        else:
            responseJSON['status'] = 'success'
            responseJSON['message'] = 'Wrong credentials'
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'No such user'

    return jsonify(responseJSON)

def register(args):
    responseJSON = {
        'status': None,
        'message': None,
        'username': None,
        'token': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("username")).first()
    if (db_user_check is None or db_user_check.role_id == 1):
        password = args.get("password")
        salt = swiftcrypt.Salts().generate_salt(16).encode('utf-8').hex()
        hashedPass = swiftcrypt.Hash().hash_password(password, salt, "sha512")

        new_record = user_db.User()
        new_record.username = args.get("username")
        new_record.password = hashedPass
        new_record.salt = salt
        new_record.pin = int(args.get("pin"))
        new_record.bank_id = int(args.get("bank_id"))
        new_record.role_id = int(args.get("role_id"))
        new_record.name = args.get("name")
        new_record.address = args.get("address")
        new_record.phone_nbr = args.get("phone_nbr")
        new_record.job = args.get("job")

        db.session.add(new_record)
        db.session.commit()
        responseJSON['status'] = 'success'
        responseJSON['username'] = args.get("username")
        responseJSON['token'] = salt
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'User already exists'

    return jsonify(responseJSON)

def modify(args):
    responseJSON = {
        'status': None,
        'message': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.role_id == 1):
        db_user_to_update = user_db.User.query.filter_by(username=args.get("username")).first()
        if (args.get('action') == 'update'):
            db_user_to_update.bank_id = args.get("bank_id")
            db_user_to_update.role_id = args.get("role_id")
            db_user_to_update.name = args.get("name")
            db_user_to_update.address = args.get("address")
            db_user_to_update.phone_nbr = args.get("phone_nbr")
            db_user_to_update.job = args.get("job")

            db.session.commit()
            responseJSON['status'] = 'success'
            responseJSON['message'] = 'Successful update'
        elif (args.get('action') == 'delete'):
            db.session.delete(db_user_to_update)
            db.session.commit()
            responseJSON['status'] = 'success'
            responseJSON['message'] = 'Successful delete'
        else:
            responseJSON['status'] = 'fail'
            responseJSON['message'] = 'Unknown command'
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'User is not found or you do not have rights to review'

    return jsonify(responseJSON)

def show_info(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }
    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and (db_user_check.role_id == 1 or
                                       (db_user_check.username == args.get("requestor") and
                                       db_user_check.salt == args.get("token")))):
        db_user_to_update = user_db.User.query.filter_by(username=args.get("username")).first()
        responseJSON['status'] = 'success'
        responseJSON['message'] = 'Successful extract'
        responseJSON['data'] = db_user_to_update.serialize
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'User is not found or you do not have rights to review'

    return jsonify(responseJSON)

def change_password(args):
    responseJSON = {
        'status': None,
        'message': None,
        'data': None
    }

    db_user_check = user_db.User.query.filter_by(username=args.get("requestor")).first()
    if (db_user_check is not None and db_user_check.salt == args.get('token')):
        prevHashedPass = swiftcrypt.Hash().hash_password(args.get("previous_password"), db_user_check.salt, "sha512")
        if (db_user_check.password == prevHashedPass):
            hashedPass = swiftcrypt.Hash().hash_password(args.get("new_password"), db_user_check.salt, "sha512")
            db_user_check.password = hashedPass
            db.session.commit()
            responseJSON['status'] = 'success'
            responseJSON['message'] = 'Successful password change'
        else:
            responseJSON['status'] = 'fail'
            responseJSON['message'] = 'Old password is wrong'
    else:
        responseJSON['status'] = 'fail'
        responseJSON['message'] = 'User is not found'

    return jsonify(responseJSON)