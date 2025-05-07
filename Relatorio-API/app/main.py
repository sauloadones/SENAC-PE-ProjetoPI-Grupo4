from flask import Flask, request, send_file
from app.relatorio import gerar_pdf, gerar_excel, gerar_word

app = Flask(__name__)

@app.route('/gerar-relatorio', methods=['POST'])
def gerar_relatorio():
    dbfile = request.files['dbfile']
    tipo = request.form.get('tipo', 'pdf')  # 'pdf', 'excel' ou 'word'

    if tipo == 'pdf':
        output_path = gerar_pdf(dbfile)
    elif tipo == 'excel':
        output_path = gerar_excel(dbfile)
    elif tipo == 'word':
        output_path = gerar_word(dbfile)
    else:
        return "Tipo inválido", 400

    return send_file(output_path, as_attachment=True)

if __name__ == '__main__':
    import os
    app.run(host='0.0.0.0', port=int(os.environ.get("PORT", 5000)))
