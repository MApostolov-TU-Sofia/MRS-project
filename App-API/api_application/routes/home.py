from datetime import datetime
from flask import render_template, request
from api_application import app
from api_application.controller.home import home_index

@app.route('/')
def home_route():
    return home_index()

