/*
This script prepends the <header> tag into the body of whatever page it is
included in; this is basically an attempt at templating using jQuery.
This script also gives the navbar some custom styling that allows it to change
to fixed when the window scrolls down far enough.
*/

(function() {
  function main($) {
    $(document).ready(function() {
      $('body').prepend(
        '<header>' +
          '<div id="header">' +
            '<a href="./classpage.html">' +
              '<h2 class="header-text">Ohlone College | CS-175 | Fall 2015</h2>' +
            '</a>' +
            '<a href="https://www.linkedin.com/in/wolfgangcstrack">' +
              '<h2 class="header-text header-name">Wolfgang C. Strack</h2>' +
            '</a>' +
          '</div>' +
          '<div id="navbar">' +
            '<a class="nav-link" href="./classpage.html">Home</a>' +
            '<a class="nav-link" href="./classpage.html#assignments">Assignments</a>' +
            '<a class="nav-link" href="./final/">Project</a>' +
          '</div>' +
        '</header>'
      );
    });
  }

  defer_jQuery(main); // global function in common-includes.js
})();
