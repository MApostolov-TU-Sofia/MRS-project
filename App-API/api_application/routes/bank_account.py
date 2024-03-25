from datetime import datetime
from flask import render_template, request
from api_application import app
from api_application.controller.bank_account import view, view_all, view_by, create, modify

def getIData(request):
    iData = request.args.to_dict()
    if (request.method == 'POST'):
        iData = request.get_json()
    return iData

@app.route('/bank_account/view', methods=['GET'])
def bank_account_view_route():
    return view(getIData(request))

@app.route('/bank_account/view_all', methods=['GET'])
def bank_account_view_all_route():
    return view_all(getIData(request))

@app.route('/bank_account/view_by', methods=['GET'])
def bank_account_view_by():
    return view_by(getIData(request))

@app.route('/bank_account/create', methods=['POST', 'GET'])
def bank_account_create_route():
    return create(getIData(request))

@app.route('/bank_account/modify', methods=['POST', 'GET'])
def bank_account_modify_route():
    return modify(getIData(request))
