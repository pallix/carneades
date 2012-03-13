PM.show_questions = function(questions, questionlist, on_submit) {
    questionlist.append('<h2>{0}</h2>'.format(questions[0].category_name));
    _.map(questions, function(q) { PM.show_question(q, questionlist); });
    
    var button_id = UTILS.gen_id();
    questionlist.append('<input type="button" value="submit" id="submit{0}"/>'.format(button_id));
    questionlist.append('<hr/>');
    $('#submit' + button_id).click(on_submit);
};

PM.show_question = function(question, questionlist) {
    // by convention the id of the input for the question N is qN
    var widget_to_html = {
        text: function(id, proposed_answers, formal_answers) {
            return '<input class="inputfield" type="text" id="q{0}" />'.format(id);
        },
        radio: function(id, proposed_answers, formal_answers) {
            var html = "";
            _.each(formal_answers, function(formal_answer, index) {
                       html += '<input id="q{0}" class="inputfield radiofield radiobutton" name="inputq{0}" value="{1}" type="radio" />{2} '
                           .format(id, formal_answer, proposed_answers[index]);
                   });
            return html;
        },
        select: function(id, proposed_answers, formal_answers) {
            var html = '<select>';
            
            _.each(formal_answers, function(formal_answer, index) {
                       html += '<option id="q{0}" class="inputfield dropdown-menu" value="{1}">{2}</option>'
                           .format(id, formal_answer, proposed_answers[index]);
                   });
            html += '</select>';
            return html;
        }
    };

    var question_widget = widget_to_html[question.widget](question.id, question.answers, question.formalanswers);
    questionlist.append('<p>{0}</p>'.format(question.hint == null ? "" : question.hint));
    var question_html = PM.get_question_html(question.question, question_widget);
    questionlist.append(question_html);
    questionlist.append('<br/>');
};

PM.get_question_html = function(question, widget) {
    var variable = /\?[a-zA-Z_0-9-]+/;
    var parts = question.split(variable);
    
    if(parts.length == 2) {
        return parts.join(widget);         
    } else {
        return question + widget;
    } 
}