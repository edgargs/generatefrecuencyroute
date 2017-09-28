
import groovy.sql.Sql

import groovy.transform.Field

//class SendTrama{
        
    @Field def conMatrix

    // groovy -cp ./sqljdbc42.jar GenerateFrecuencyRoute.groovy

    void loadData(Sql conMatrix) {
        //Carga rutas
        conMatrix.eachRow('''SELECT g.accountID,g.routeID FROM MTXRoute AS g
WHERE accountID = 9
--and routeID = 3
'''
) {
            fila -> 
                println fila
                creaDispatch(conMatrix,fila.accountID,fila.routeID)
        }

        conMatrix.close()
    }

void creaDispatch(Sql sql,int accountID, int routeID) {
    
    sql.execute( "{call dbo.MTX_sp_FrecuencyHistoryDay(?, ?)}" ,
             [accountID,routeID] ) 
	sql.execute( "{call dbo.MTX_sp_FrecuencyResumeWeek(?, ?)}" ,
             [accountID,routeID] ) 
}

void updateVariables(args) {
    
    confEnv = (args.size() > 0)?args[0]:"DESA"

    //CONNECION
    def connBBDD

    if(confEnv=="PROD"){
        def connPROD = ["192.168.1.104",1433,"matrix",'3X3tn$$$w']
        
        connBBDD = connPROD
        
    } else if(confEnv=="LOCAL"){
        def connDESA = ["192.168.5.35",1433,"sa","hcm123"]
        
        connBBDD = connDESA
        
    } else{
        def connDESA = ["192.168.5.35",1433,"sa","hcm123"]
        
        connBBDD = connDESA
        
    }
    conMatrix = Sql.newInstance("jdbc:sqlserver://${connBBDD[0]}:${connBBDD[1]};databaseName=MATRIX",connBBDD[2],connBBDD[3],"com.microsoft.sqlserver.jdbc.SQLServerDriver")

}
//}

println args

updateVariables(args)

loadData(conMatrix)
