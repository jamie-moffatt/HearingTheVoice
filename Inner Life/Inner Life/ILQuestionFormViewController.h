//
//  ILQuestionFormViewController.h
//  Inner Life
//
//  Created by Matthew Bates on 20/07/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ILDataSingleton.h"

@interface ILQuestionFormViewController : UITableViewController
{
    @private
    ILDataSingleton *data;
}

@property NSInteger currentSession;

@end
