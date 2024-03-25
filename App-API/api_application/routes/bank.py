from datetime import datetime
from flask import render_template, request
from api_application import app
from api_application.controller.bank import view, view_all, create, modify

def getIData(request):
    iData = request.args.to_dict()
    if (request.method == 'POST'):
        iData = request.get_json()
    return iData

@app.route('/bank/view', methods=['GET'])
def bank_view_route():
    return view(getIData(request))

@app.route('/bank/view_all', methods=['GET'])
def bank_view_all_route():
    return view_all(getIData(request))

@app.route('/bank/create', methods=['POST', 'GET'])
def bank_create_route():
    return create(getIData(request))

@app.route('/bank/modify', methods=['POST', 'GET'])
def bank_modify_route():
    return modify(getIData(request))
