from datetime import datetime
from flask import render_template, jsonify
from api_application import app

def home_index():
    return jsonify({
        'status': 'running'
    })