# whodata.py
import os
import sys 
import time
import json

example = '{"login_user": {"id": "1000", "name": "vagrant"}, "group": {"id": "0", "name": "root"}, "user": {"id": "0", "name": "root"}, "proccess": {"ppid": "8698", "id": "10554", "name": "/usr/bin/nano"}, "effective_user": {"id": "0", "name": "root"}}'

class Watcher(object):
    running = True
    refresh_delay_secs = 1

    # Constructor
    def __init__(self, watch_file, call_func_on_change=None, *args, **kwargs):
        self._cached_stamp = 0
        self.filename = watch_file
        self.call_func_on_change = call_func_on_change
        self.args = args
        self.ctr = 0
        self.kwargs = kwargs

    # Look for changes
    def look(self):
        stamp = os.stat(self.filename).st_mtime
        if stamp != self._cached_stamp:
            self._cached_stamp = stamp
            # File has changed, so do something...
            if self.call_func_on_change is not None:
                self.ctr += 1
                f_read = open(self.filename, "r")
                last_line = f_read.readlines()[-1]
                f_read.close()
                self.call_func_on_change(last_line, **self.kwargs)

    # Keep watching in a loop        
    def watch(self):
        while self.running: 
            try: 
                # Look for changes
                time.sleep(self.refresh_delay_secs) 
                self.look() 
            except KeyboardInterrupt: 
                print('\nDone') 
                break 
            except IOError:
                # Action on file not found
                pass
            except: 
                print('Unhandled error: %s' % sys.exc_info()[0])

# Call this function each time a change happens
def custom_action(text):
    data = json.loads(text)
    print json.dumps(data['syscheck']['audit'])

watch_file = sys.argv[1]
watcher = Watcher(watch_file, custom_action)  # also call custom action function
watcher.watch()  # start the watch going


