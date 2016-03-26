module.exports = function(grunt) {
	grunt.initConfig({
		pkg:grunt.file.readJSON('package.json'),
		banner: '/*! <%= pkg.title || pkg.name %> - v<%= pkg.version %> - ' +
	      '<%= grunt.template.today("yyyy-mm-dd") %>\n' +
	      '<%= pkg.homepage ? "* " + pkg.homepage + "\\n" : "" %>' +
	      '* Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author %>;' +
	      ' Licensed <%= pkg.licenses %> */\n',
      	fileName:'angular-ad',
      	// Task configuration.
	    clean: {
	      src: ['dist']
	    },
	    concat: {
	      options: {
	        banner: '<%= banner %>',
	        stripBanners: true
	      },
	      dist: {
	        src: ['src/<%= fileName %>.js'],
	        dest: 'dist/<%= fileName %>.js'
	      },
	    },
		uglify: {
	      options: {
	        banner: '<%= banner %>'
	      },
	      dist: {
	        src: '<%= concat.dist.dest %>',
	        dest: 'dist/<%= fileName %>.min.js'
	      }
	   },
	   jshint: {
      		options: {
	          jshintrc: '.jshintrc'
        	},
        	all: ['src/**/*.js','Gruntfile.js']
	   },
	   watch: {
	      src: {
	        files: '<%= jshint.all %>',
	        tasks: ['jshint','clean','concat','uglify']
	      }
	    }
	});
	grunt.loadNpmTasks('grunt-contrib-clean');
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-contrib-jshint');
	grunt.loadNpmTasks('grunt-contrib-watch');
	
  	grunt.registerTask('default', ['jshint','clean','concat','uglify']);
  	
};