\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{ps-exercise}[2014/10/14 Proseminar exercise class]
\LoadClass{scrartcl}

% load required packages
\usepackage[utf8]{inputenc}
\usepackage[T1]{fontenc}
\usepackage{lmodern}
\usepackage{amsmath,amsthm,amssymb}
\usepackage{mathtools}
% load optional packages
\usepackage{tikz}
\usetikzlibrary{matrix}
\usepackage{enumitem}
% load document specific packages
\usepackage{fancyhdr}
\usepackage{lastpage}

%%% considerable packages: ntheorem, embrac

% commands (new)
\newcommand*{\ssep}{\,;\,}
\newcommand*{\fsep}{\,:\,}
\newcommand*{\Complement}[1]{{#1}^{\mathsf{c}}}
\newcommand*{\conj}[1]{\overline{#1}}
\newcommand*{\rfrac}[2]{{#1/#2}}
\newcommand*{\closure}[1]{\overline{#1}}
\newcommand*{\comp}{\circ}

% commands (renew) 
\renewcommand*{\restriction}{|}
\renewcommand{\labelenumi}{(\arabic{enumi})}

% paired delimiters
%% note that \command* uses unstarred brackets
\DeclarePairedDelimiter{\abs}{\lvert}{\rvert} 
\DeclarePairedDelimiter{\norm}{\lVert}{\rVert}
\DeclarePairedDelimiter{\innerprod}{\langle}{\rangle}

% math operators
\DeclareMathOperator{\id}{id}
\DeclareMathOperator{\Hom}{Hom}
\DeclareMathOperator{\lin}{lin}
\DeclareMathOperator{\im}{im}
\DeclareMathOperator{\cre}{Re}
\DeclareMathOperator{\cim}{Im}

% small hacks
\newcommand*{\skipfirstline}[1][]{~ #1}
%% solves problem: skip first line in a theorem environment, when starting with enumerate 
%% more information: http://goo.gl/SQ17U9

% front page %
%%% come here %%%

% custom header %
\pagestyle{fancy} %name
\fancyhf{} %clear all headers
\fancyhead[L]{\headrule\vspace{4.5pt}\textsc{\textbf{\@subject}}
			\\\@title} %header left
\fancyhead[C]{} %header center
\fancyhead[R]{\@author
			\\\@date} %header right
\renewcommand{\headrulewidth}{0.4pt} %upper separation line
\fancyfoot[C]{Page \thepage\ of \pageref{LastPage}} %page number
\renewcommand{\footrulewidth}{0.4pt} %lower separation line
