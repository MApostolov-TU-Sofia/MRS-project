from datetime import datetime
from flask import render_template, request
from api_application import app
from api_application.controller.credit_card import view, view_all, view_by, create, modify

def getIData(request):
    iData = request.args.to_dict()
    if (request.method == 'POST'):
        iData = request.get_json()
    return iData

@app.route('/credit_card/view', methods=['GET'])
def credit_card_view_route():
    return view(getIData(request))

@app.route('/credit_card/view_all', methods=['GET'])
def credit_card_view_all_route():
    return view_all(getIData(request))

@app.route('/credit_card/view_by', methods=['GET'])
def credit_card_view_by():
    return view_by(getIData(request))

@app.route('/credit_card/create', methods=['POST', 'GET'])
def credit_card_create_route():
    return create(getIData(request))

@app.route('/credit_card/modify', methods=['POST', 'GET'])
def credit_card_modify_route():
    return modify(getIData(request))
