from datetime import datetime
from flask import render_template, request
from api_application import app
from api_application.controller.transaction import view, view_all, view_by, create, modify

def getIData(request):
    iData = request.args.to_dict()
    if (request.method == 'POST'):
        iData = request.get_json()
    return iData

@app.route('/transaction/view', methods=['GET'])
def transaction_view_route():
    return view(getIData(request))

@app.route('/transaction/view_all', methods=['GET'])
def transaction_view_all_route():
    return view_all(getIData(request))

@app.route('/transaction/view_by', methods=['GET'])
def transaction_view_by():
    return view_by(getIData(request))

@app.route('/transaction/create', methods=['POST', 'GET'])
def transaction_create_route():
    return create(getIData(request))

@app.route('/transaction/modify', methods=['POST', 'GET'])
def transaction_modify_route():
    return modify(getIData(request))
