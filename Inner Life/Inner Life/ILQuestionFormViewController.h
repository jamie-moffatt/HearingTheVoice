//
//  ILQuestionFormViewController.h
//  Inner Life
//
//  Created by Matthew Bates on 26/07/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ILDataSingleton.h"
#import "ILQuestionProgressBar.h"

@interface ILQuestionFormViewController : UIViewController <UITableViewDelegate, UITableViewDataSource>
{
@private ILDataSingleton *data;
}

@property UITableView *tableView;
@property ILQuestionProgressBar *questionProgressBar;

- (void)previousButton:(id)sender;
- (void)nextButton:(id)sender;

@property NSInteger currentSession;
@property UISlider *slider;

@end

