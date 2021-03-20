 cout<<"> Enter commit message "<<endl;
            string msg;
            cin>>msg;
            string commit_msg = "git commit -m \"" + msg + "\"";
            system("git add .");
            cout<<commit_msg<<" ";