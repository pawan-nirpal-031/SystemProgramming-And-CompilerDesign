#include <bits/stdc++.h>
using namespace std;


class GitEngine{
    private:
    
        string TextOutputExecute(const char* cmd) {
            array<char, 1024> buffer;
            string result;
            unique_ptr<FILE, decltype(&pclose)> pipe(popen(cmd, "r"), pclose);
            if (!pipe) {
                throw runtime_error("popen() failed!");
            }
            while (fgets(buffer.data(), buffer.size(), pipe.get()) != nullptr) {
                result += buffer.data();
            }
            return result;
        }

        void SyncronizeAllToMasterRemoteUtil(string commit_message){
            if(commit_message.size()==0){
                cout<<"Commit message cannot be empty"<<endl;
            }else{
                system("git add .");
                string cmd="git commit -m";
                cmd += "\"" + commit_message+ "\"";
                system(&cmd[0]);
                system("git push origin master");
            }
        }

        void InitalizeGitRepositoryUtil(string local_repo_path, string commit_message, string files_to_add, string remote_url){
            if(remote_url.size()==0){
                cout<<"> Please specify remote URL"<<endl;
                return;
            }
            if(local_repo_path.size()!=0){
                string cmd = "cd ";
                cmd+=local_repo_path;
                system(&cmd[0]);
            }
            if(commit_message==""){
                commit_message = "\"first commit\"";
            }else{
                commit_message = "\"" + commit_message+ "\"";
            }
            if(files_to_add==""){
                files_to_add = ".";
            }

            string process[] = {"git init",  "git add "+files_to_add,  "git commit -m "+commit_message,  "git remote add origin "+remote_url,  "git remote -v",  "git push origin master"};
            for(int i =0;i<6;i++){
                system(&process[i][0]);
            }
        }

    public:

        void ForcePush(){
            system("git push origin master --force");
        }

        string CurrentRemoteRepository(){
            return TextOutputExecute("git remote -v");
        }

        void InitalizeGitRepository(){
            string local_repo;
            cout<<"> Enter local repository"<<endl;
            cin>>local_repo;
            
            string files_to_add;
            cout<<"> Enter Files to add"<<endl;
            cin>>files_to_add;
            
            cout<<"> Enter commit message"<<endl;
            string commit;
            cin>>commit;
            
            cout<<"> Enter remote repository"<<endl;
            string remote_repository;
            cin>>remote_repository;
            InitalizeGitRepositoryUtil(local_repo,commit,files_to_add,remote_repository);
        }

        void SyncAllToMasterRemote(){
            cout<<"> Enter commit message "<<endl;
            string commit;
            cin>>commit;
            this->SyncronizeAllToMasterRemoteUtil(commit);
        }

        inline bool FilExist(const std::string& name) {
            ifstream file(name); 
            return ((not file)?0:1);
        }

};



int main(int argc, char const *argv[]){
   GitEngine g;
   g.InitalizeGitRepository();
   return 0;
}

