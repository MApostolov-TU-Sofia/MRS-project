from datetime import datetime
from flask import render_template, request
from api_application import app
from api_application.controller.user import login, register, modify, show_info, change_password

def getIData(request):
    iData = request.args.to_dict()
    if (request.method == 'POST'):
        iData = request.get_json()
    return iData

@app.route('/user/login', methods=['POST', 'GET'])
def user_login_route():
    return login(getIData(request))

@app.route('/user/register', methods=['POST'])
def user_register_route():
    return register(getIData(request))

@app.route('/user/modify', methods=['POST', 'GET'])
def user_modify_route():
    return modify(getIData(request))

@app.route('/user/show_info', methods=['GET'])
def user_show_info_route():
    return show_info(getIData(request))
@app.route('/user/change_password', methods=['POST'])
def user_change_password_route():
    return change_password(getIData(request))
