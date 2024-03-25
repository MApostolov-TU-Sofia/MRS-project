from datetime import datetime
from flask import render_template, request
from api_application import app
from api_application.controller.role import view, view_all, create, modify

def getIData(request):
    iData = request.args.to_dict()
    if (request.method == 'POST'):
        iData = request.get_json()
    return iData

@app.route('/role/view', methods=['GET'])
def role_view_route():
    return view(getIData(request))

@app.route('/role/view_all', methods=['GET'])
def role_view_all_route():
    return view_all(getIData(request))

@app.route('/role/create', methods=['POST', 'GET'])
def role_create_route():
    return create(getIData(request))

@app.route('/role/modify', methods=['POST', 'GET'])
def role_modify_route():
    return modify(getIData(request))
